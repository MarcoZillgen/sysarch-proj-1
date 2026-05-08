package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class nBitAND(n: Int) extends Module {
  val a   = IO(Input(Vec(n, Bool())))
  val out = IO(Output(Bool()))

  val acc = Wire(Vec(n, Bool()))
  acc(0) := a(0)

  for (i <- 1 until n) {
    acc(i) := And(acc(i - 1), a(i))
  }

  out := acc(n - 1)
}
