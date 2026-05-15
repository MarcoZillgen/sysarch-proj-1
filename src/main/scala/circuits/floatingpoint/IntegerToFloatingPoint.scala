package sysarch.circuits.floatingpoint

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class IntegerToFloatingPoint extends Module {
  val intInput    = IO(Input(Vec(32, Bool())))
  val floatOutput = IO(Output(Vec(32, Bool())))

  /*
  first bit is sign bit, copy as is
  exponent = position of leadin one + 127
  mantissa = next 23 bits after leading one (pad with 0s if less than 23 bits)
  if 0, set exponent and mantissa as 0
   */
  val sign = Wire(Bool())
  sign := intInput(31)
  val leadingOne = Module(new LeadingOneDetector)
  leadingOne.a := intInput.slice(0, 31)

  // Exponent
  val pos = Wire(Vec(8, Bool()))
  pos.slice(0, 5) := leadingOne.position
  pos.slice(5, 8) := Vec.fill(3)(false.B)
  val bias = Wire(Vec(8, Bool()))
  bias.slice(0, 7) := Vec.fill(7)(true.B)
  bias(7)          := false.B

  val exp = Module(new nBitAdderSubtractor(8))
  exp.a          := bias
  exp.b          := pos
  exp.enable_sub := false.B

// Mantissa
  val mantissa = Wire(Vec(23, Bool()))

  val candidates = Wire(Vec(31, Vec(23, Bool())))
  for (p <- 0 until 31) {
    for (j <- 0 until 23) {
      val srcBit = p - 1 - j // bit of magnitude just below the leading 1
      candidates(p)(j) := false.B
      if (srcBit >= 0) {
        if (srcBit < 31) {
          candidates(p)(j) := intInput(srcBit)
        }
      }
    }
  }
  for (j <- 0 until 23) {
    val bits = Wire(Vec(31, Bool()))
    for (p <- 0 until 31) {
      bits(p) := And(candidates(p)(j), leadingOne.isLeading(p))
    }
    val orAll = Module(new nBitOR(31))
    orAll.a     := bits
    mantissa(j) := orAll.out
  }
// 0-case
  val zeroResult = Wire(Vec(32, Bool()))
  zeroResult.slice(0, 31) := Vec.fill(31)(false.B)
  zeroResult(31)          := sign

  val result = Wire(Vec(32, Bool()))
  result(31)           := sign
  result.slice(23, 31) := exp.sum
  result.slice(0, 23)  := mantissa

// MUX between zero and normal result based on found flag
  val mux = Module(new Mux(32))
  mux.a   := zeroResult
  mux.b   := result
  mux.sel := leadingOne.found

  floatOutput := mux.out

}
