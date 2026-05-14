package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class FullAdder extends Module {
  val a    = IO(Input(Bool()))
  val b    = IO(Input(Bool()))
  val cin  = IO(Input(Bool()))
  val sum  = IO(Output(Bool()))
  val cout = IO(Output(Bool()))

  /*
    a | b | cin | sum | cout
    0 | 0 | 0   | 0   | 0
    1 | 0 | 0   | 1   | 0
    0 | 1 | 0   | 1   | 0
    1 | 1 | 0   | 0   | 1
    0 | 0 | 1   | 1   | 0
    1 | 0 | 1   | 0   | 1
    0 | 1 | 1   | 0   | 1
    1 | 1 | 1   | 1   | 1
   */

  sum  := Xor(a, Xor(b, cin))
  cout := Or(And(a, b), Or(And(a, cin), And(b, cin)))
}
