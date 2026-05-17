  package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class nBitAdderSubtractor(width: Int) extends Module {
  val a          = IO(Input(Vec(width, Bool())))
  val b          = IO(Input(Vec(width, Bool())))
  val enable_sub = IO(Input(Bool()))
  val sum        = IO(Output(Vec(width, Bool())))
  val cout       = IO(Output(Bool()))

  // 1. create carries vector w/ initial carry to be enable_sub
  val carries = Wire(Vec(width + 1, Bool()))
  carries(0) := enable_sub

  // 2. compute sum_n = fa_0(a_n, b_n, c_n)
  // 3. set next carry out: c_(n+1) = fa_1(a_n, b_n, c_n)
  // 2. and 3. => its just a ripple carry adder
  for (i <- 0 until width) {
    val xor_b = Xor(enable_sub, b(i))

    val fa = Module(new FullAdder())
    fa.a   := a(i)
    fa.b   := xor_b
    fa.cin := carries(i)

    sum(i)         := fa.sum
    carries(i + 1) := fa.cout
  }

  // 4. invert last carry out on substraction
  cout := Xor(carries(width), enable_sub)
}
