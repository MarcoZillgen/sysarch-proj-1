package sysarch.circuits.floatingpoint

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class FloatingPointNaN extends Module {
  val in   = IO(Input(Vec(32, Bool())))
  val out = IO(Output(Bool()))

  /*
    e all ones && m some one => NaN
   */

  val eAll = Wire(Bool())
  eAll := true.B
  for (i <- 23 until 31) {
    eAll := And(eAll, a(i))
  }

  val mSome = Wire(Bool())
  mSome := false.B
  for (i <- 0 until 23) {
    mSome := Or(mSome, a(i))
  } 
  out := And(eAll, mSome)
}
