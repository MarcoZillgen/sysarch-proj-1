package sysarch.circuits.integer

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class SignedMagnitudeToOnesComplement(width: Int) extends Module {
  val signedMagnitude = IO(Input(Vec(width, Bool())))
  val onesComplement  = IO(Output(Vec(width, Bool())))

  /*
    given a_n we have two cases
    - a_(n-1) == 0: we copy a into b
    - a_(n-1) == 1: we invert a_(n-2) to a_0 and copy a into b
   */

  val len     = width - 1
  val signBit = signedMagnitude(len)

  val mux = Module(new Mux(len))
  for (i <- 0 until len) {
    mux.a(i) := signedMagnitude(i)
    mux.b(i) := Not(signedMagnitude(i))
  }
  mux.sel := signBit

  for (i <- 0 until width) {
    onesComplement(i) := mux.out(i)
  }
}
