package sysarch.circuits.integer

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class TwosComplementToSignedMagnitude(width: Int) extends Module {
  val twosComplement  = IO(Input(Vec(width, Bool())))
  val signedMagnitude = IO(Output(Vec(width, Bool())))

  /*
    Two cases:
    - a_(n-1) == 0: copy a into b
    - a_(n-1) == 1: Concat(1,Add(Not(a_(n-2...)),1))

    ! $-2^(n-1)$ cannot be mapped, thus return value is ignored
   */

  val len     = width - 1
  val lastBit = twosComplement(len)

  val one = Wire(Vec(len, Bool()))
  one    := Vec.fill(len)(false.B)
  one(0) := true.B

  val not = Module(new nBitNOT(len))
  for (i <- 0 until len) {
    not.a(i) := twosComplement(i)
  }

  val add = Module(new nBitAdderSubtractor(len))
  add.enable_sub := false.B
  add.a          := one
  add.b          := not.out

  val mux = Module(new Mux(width))
  mux.b(len) := true.B
  for (i <- 0 until len) {
    mux.b(i) := add.sum(i)
  }
  mux.a   := twosComplement
  mux.sel := lastBit

  signedMagnitude := mux.out
}
