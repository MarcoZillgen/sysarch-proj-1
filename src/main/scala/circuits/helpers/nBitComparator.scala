package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class nBitComparator(width: Int) extends Module {
  val a  = IO(Input(Vec(width, Bool())))
  val b  = IO(Input(Vec(width, Bool())))
  val gt = IO(Output(Bool()))
  val eq = IO(Output(Bool()))

  // sub = a - b
  val sub = Module(new nBitAdderSubtractor(width))
  sub.a          := a
  sub.b          := b
  sub.enable_sub := true.B

  // if a = b => sub = 0
  // sub = 0 => sub = 0...0 => no sub(i) is 1
  val orAll = Module(new nBitOR(width))
  orAll.a := sub.sum
  val isNotZero = orAll.out
  eq := Not(isNotZero)

  // a < b <=> sub.cout = 1
  // therefore not(sub.cout) = a >= b
  // therefore notZero and (a>=b) => a > b
  val a_geq_b = Not(sub.cout)
  gt := And(a_geq_b, isNotZero)
}
