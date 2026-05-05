package sysarch.gates

import chisel3._

class NOTGate extends Module {
  val a   = IO(Input(Bool()))
  val out = IO(Output(Bool()))

  out := !a
}

object Not {
  def apply(a: Bool): Bool = {
    val notGate = Module(new NOTGate)
    notGate.a := a
    notGate.out
  }
}
