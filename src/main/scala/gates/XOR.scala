package sysarch.gates

import chisel3._

class XORGate extends Module {
  val a   = IO(Input(Bool()))
  val b   = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := a ^ b
}

object Xor {
  def apply(a: Bool, b: Bool): Bool = {
    val xorGate = Module(new XORGate)
    xorGate.a := a
    xorGate.b := b
    xorGate.out
  }
}
