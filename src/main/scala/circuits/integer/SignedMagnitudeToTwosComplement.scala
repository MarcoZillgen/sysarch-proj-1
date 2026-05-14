package sysarch.circuits.integer

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class SignedMagnitudeToTwosComplement(width: Int) extends Module {
  val signedMagnitude = IO(Input(Vec(width, Bool())))
  val twosComplement  = IO(Output(Vec(width, Bool())))

  val neg = Module(new nBitNOT(width - 1)) {
    neg.a := signedMagnitude.slice(0, width - 2)
  }

  val inverted = Wire(Vec(width, Bool()))
  inverted.slice(0, width - 2) := neg.out
  inverted(width - 1)          := signedMagnitude(width - 1)

  val twosc = Module(new nBitAdderSubtractor(width)) {
    twosc.a          := inverted
    twosc.b          := (true.B +: Vec.fill(width - 1)(false.B))
    twosc.enable_sub := false.B
  }

  val res = Module(new Mux(width)) {
    res.a   := signedMagnitude
    res.b   := twosc.sum
    res.sel := signedMagnitude(width - 1)
  }
  twosComplement := res.out

}
