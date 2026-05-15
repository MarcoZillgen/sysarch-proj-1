package sysarch.circuits.floatingpoint

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class FloatingPointToInteger extends Module {
  val floatInput = IO(Input(Vec(32, Bool())))
  val intOutput  = IO(Output(Vec(32, Bool())))

  /*
  extract position of leading one = (exponent - 127)
  reconstruct Mantissa with leading one = 1 mmmmmmmmmmm...
  Int = Mantissa x 2^pos (shift by pos)
  truncate (based on p)
   */

  val sign     = floatInput(31)
  val exponent = Wire(Vec(8, Bool()))
  exponent := floatInput.slice(23, 31)
  val mantissa = Wire(Vec(23, Bool()))
  mantissa := floatInput.slice(0, 23)

  // compute position = exponent - 127
  val bias = Wire(Vec(8, Bool()))
  bias.slice(0, 7) := Vec.fill(7)(true.B)
  bias(7)          := false.B

  val expSub = Module(new nBitAdderSubtractor(8))
  expSub.a          := exponent
  expSub.b          := bias
  expSub.enable_sub := true.B
  val p = expSub.sum

  val pNegative = p(7)
  val pIs31     = Module(new nBitAND(5))
  pIs31.a := p.slice(0, 5)
  val pTooBig = And(Not(p(7)), Or(Or(p(5), p(6)), pIs31.out))

  // reconstructing Mantissa
  val full = Wire(Vec(24, Bool()))
  full.slice(0, 23) := mantissa
  full(23)          := true.B

  val magnitude = Wire(Vec(31, Bool()))

  val candidates = Wire(Vec(32, Vec(31, Bool())))
  for (pVal <- 0 until 32) {
    for (i <- 0 until 31) {
      val srcBit = i - pVal + 23
      candidates(pVal)(i) := false.B
      if (srcBit >= 0) {
        if (srcBit < 24) {
          candidates(pVal)(i) := full(srcBit)
        }
      }
    }
  }

  val isExact = Wire(Vec(32, Bool()))
  for (pVal <- 0 until 32) {
    val matches = Wire(Vec(8, Bool()))

    if (pVal % 2 == 1) { matches(0) := p(0) }
    else { matches(0) := Not(p(0)) }
    if ((pVal / 2) % 2 == 1) { matches(1) := p(1) }
    else { matches(1) := Not(p(1)) }
    if ((pVal / 4) % 2 == 1) { matches(2) := p(2) }
    else { matches(2) := Not(p(2)) }
    if ((pVal / 8) % 2 == 1) { matches(3) := p(3) }
    else { matches(3) := Not(p(3)) }
    if ((pVal / 16) % 2 == 1) { matches(4) := p(4) }
    else { matches(4) := Not(p(4)) }
    if ((pVal / 32) % 2 == 1) { matches(5) := p(5) }
    else { matches(5) := Not(p(5)) }
    if ((pVal / 64) % 2 == 1) { matches(6) := p(6) }
    else { matches(6) := Not(p(6)) }
    if (pVal / 128 == 1) { matches(7) := p(7) }
    else { matches(7) := Not(p(7)) }

    val andAll = Module(new nBitAND(8))
    andAll.a      := matches
    isExact(pVal) := andAll.out
  }

  for (i <- 0 until 31) {
    val bits = Wire(Vec(32, Bool()))
    for (pVal <- 0 until 32) {
      bits(pVal) := And(candidates(pVal)(i), isExact(pVal))
    }
    val orAll = Module(new nBitOR(32))
    orAll.a      := bits
    magnitude(i) := orAll.out
  }

  // zero: exponent = 0 (denormal or zero input)
  val expIsZero = Wire(Vec(8, Bool()))
  for (b <- 0 until 8) expIsZero(b) := Not(exponent(b))
  val expZeroAnd = Module(new nBitAND(8))
  expZeroAnd.a := expIsZero
  val isZero = expZeroAnd.out

  // max magnitude = 0111...1 (31 ones) for out of range
  val maxMagnitude = Wire(Vec(31, Bool()))
  maxMagnitude := Vec.fill(31)(true.B)

  // zero magnitude
  val zeroMagnitude = Wire(Vec(31, Bool()))
  zeroMagnitude := Vec.fill(31)(false.B)

  val isZeroOrNeg = Or(isZero, pNegative)
  val mux1        = Module(new Mux(31))
  mux1.a   := magnitude
  mux1.b   := zeroMagnitude
  mux1.sel := isZeroOrNeg

  val mux2 = Module(new Mux(31))
  mux2.a   := mux1.out
  mux2.b   := maxMagnitude
  mux2.sel := pTooBig

  intOutput.slice(0, 31) := mux2.out
  intOutput(31)          := sign
}
