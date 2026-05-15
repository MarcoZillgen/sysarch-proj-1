package sysarch.circuits.helpers

import sysarch.chisel._
import sysarch.gates._
import sysarch.circuits.helpers._

class LeadingOneDetector extends Module {
  val a         = IO(Input(Vec(31, Bool())))
  val position  = IO(Output(Vec(5, Bool())))
  val found     = IO(Output(Bool()))
  val isLeading = IO(Output(Vec(31, Bool())))

  // seenOne(i) = true if any bit at index i or above is 1
  val seenOne = Wire(Vec(31, Bool()))
  seenOne(30) := a(30)

  for (i <- (0 until 30).reverse) {
    seenOne(i) := Or(seenOne(i + 1), a(i))
  }

  // isLeading(i) = true only for the highest set bit
  val isLead = Wire(Vec(31, Bool()))
  isLead(30) := a(30)

  for (i <- (0 until 30).reverse) {
    isLead(i) := And(a(i), Not(seenOne(i + 1)))
  }

  isLeading := isLead

  found := seenOne(0)

  // encode position into 5 bits
  for (b <- 0 until 5) {
    val bits = Wire(Vec(31, Bool()))

    for (i <- 0 until 31) {
      // hardcode 2^b for each value of b
      val bit = b match {
        case 0 => i        % 2 == 1
        case 1 => (i / 2)  % 2 == 1
        case 2 => (i / 4)  % 2 == 1
        case 3 => (i / 8)  % 2 == 1
        case 4 => (i / 16) % 2 == 1
      }

      if (bit) {
        bits(i) := isLeading(i)
      } else {
        bits(i) := false.B
      }
    }

    val orAll = Module(new nBitOR(31))
    orAll.a     := bits
    position(b) := orAll.out
  }

}
