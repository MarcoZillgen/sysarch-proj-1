package sysarch.circuits.integer

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class TwosComplementToOnesComplement(width: Int) extends Module {
  val twosComplement = IO(Input(Vec(width, Bool())))
  val onesComplement = IO(Output(Vec(width, Bool())))

  /*
  just need to subtract 1 from two's complement in case of negetive number
  calculating the conversion if needed
  then determining the case using MUX
   */

  val adder = Module(new nBitAdderSubtractor(width))
  adder.a          := twosComplement
  adder.b          := ((true.B) +: Vec.fill(width - 1)(false.B))
  adder.enable_sub := true.B

  val res = Module(new Mux(width))
  res.a   := twosComplement
  res.b   := adder.sum
  res.sel := twosComplement(width - 1)

  onesComplement := res.out

}
