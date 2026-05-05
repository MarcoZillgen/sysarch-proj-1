package sysarch.gates

import chisel3._

class NORGate extends Module {
  val a   = IO(Input(Bool()))
  val b   = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := ~(a | b)
}

object Nor {
  def apply(a: Bool, b: Bool): Bool = {
    val norGate = Module(new NORGate)
    norGate.a := a
    norGate.b := b
    norGate.out
  }
}
