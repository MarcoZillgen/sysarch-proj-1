package sysarch.gates

import chisel3._

class ANDGate extends Module {
  val a   = IO(Input(Bool()))
  val b   = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := a & b
}

object And {
  def apply(a: Bool, b: Bool): Bool = {
    val andGate = Module(new ANDGate)
    andGate.a := a
    andGate.b := b
    andGate.out
  }
}
