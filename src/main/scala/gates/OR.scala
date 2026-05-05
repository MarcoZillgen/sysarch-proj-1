package sysarch.gates

import chisel3._

class ORGate extends Module {
  val a   = IO(Input(Bool()))
  val b   = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := a | b
}

object Or {
  def apply(a: Bool, b: Bool): Bool = {
    val orGate = Module(new ORGate)
    orGate.a := a
    orGate.b := b
    orGate.out
  }
}
