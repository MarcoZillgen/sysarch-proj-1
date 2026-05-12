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

}
