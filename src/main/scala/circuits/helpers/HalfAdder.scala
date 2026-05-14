package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class HalfAdder extends Module {
  val a    = IO(Input(Bool()))
  val b    = IO(Input(Bool()))
  val sum  = IO(Output(Bool()))
  val cout = IO(Output(Bool()))

  /*
    a | b | sum | cout
    0 | 0 | 0   | 0
    1 | 0 | 1   | 0
    0 | 1 | 1   | 0
    1 | 1 | 0   | 1
   */

  sum  := Xor(a, b)
  cout := And(a, b)
}
