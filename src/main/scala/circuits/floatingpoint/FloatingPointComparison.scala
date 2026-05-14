package sysarch.circuits.floatingpoint

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

// Output: 00 for equal, 01 for floatInput1 > floatInput2, 10 for floatInput1 < floatInput2, 11 for unordered (NaN cases)

class FloatingPointComparison extends Module {
  val a                = IO(Input(Vec(32, Bool())))
  val b                = IO(Input(Vec(32, Bool())))
  val comparisonResult = IO(Output(Vec(2, Bool())))

  /*
    NaN(a) || NaN(b) : 1,1
    a == b           : 0,0
    a > b            : 1,0
    a < b            : 0,1
   */

  // NaN case
  val nanA = Module(new FloatingPointNaN())
  nanA.in := a
  val nanB = Module(new FloatingPointNaN())
  nanB.in := b

  val nanMux = Module(new Mux(2))
  nanMux.sel  := Or(nanA.out, nanB.out)
  nanMux.b(0) := true.B
  nanMux.b(1) := true.B

  // Eq case
  val eqComp = Module(new nBitComparator(32))
  eqComp.a := a
  eqComp.b := b

  val eqMux = Module(new Mux(2))
  eqMux.sel  := eqComp.eq
  eqMux.b(0) := false.B
  eqMux.b(1) := false.B

  // Sign case
  val signMux = Module(new Mux(2))
  signMux.sel  := Xor(a(31), b(31))
  signMux.b(0) := b(31)
  signMux.b(1) := a(31)

  // Value case
  val magComp = Module(new nBitComparator(31))
  for (i <- 0 until 31) { magComp.a(i) := a(i) }
  for (i <- 0 until 31) { magComp.b(i) := b(i) }
  val magLt = And(Not(magComp.gt), Not(magComp.eq))

  signMux.a(0) := Xor(magComp.gt, a(31))
  signMux.a(1) := Xor(magLt,      a(31))

  eqMux.a          := signMux.out
  nanMux.a         := eqMux.out
  comparisonResult := nanMux.out
}
