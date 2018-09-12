// Copyright (C) Maxime MORGE 2018
package org.scamata.experiments

import java.io.{BufferedWriter, FileWriter}

import akka.actor.ActorSystem
import org.scamata.core._
import org.scamata.solver._

/**
  * Main app to test Exhaustive solver vs Gift solver vs LP solver
  */
object ComputeCmax {

  val debug= true

    def main(args: Array[String]): Unit = {
      val rule: SocialRule = LCmax
      val r = scala.util.Random
      val file = s"experiments/data/min$rule.csv"
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(s"m,n," +
        s"minexhaustiveSolver$rule,openexhaustiveSolver$rule,meanexhaustiveSolver$rule,closedexhaustiveSolver$rule,maxexhaustiveSolver$rule," +
        s"mingiftSolver$rule,opengiftSolver$rule,meangiftSolver$rule,closedgiftSolver$rule,maxgiftSolver$rule,"+
        s"minlpSolver$rule,openlpSolver$rule,meanlpSolver$rule,closedlpSolver$rule,maxlpSolver$rule\n")
      for (m <- 2 to 100) {
        for (n <- 2*m to 2*m) {
          if (debug) println(s"Test configuration with $m peers and $n tasks")
          val nbPb = 20 // should be x*4
          var (exhaustiveSolverRule, giftSolverRule, swapSolverRule, lpSolverRule) =
            (List[Double](), List[Double](),  List[Double](), List[Double]())
          for (o <- 1 to nbPb) {
            if (debug) println(s"Configuration $o")
            val pb = MATA.randomProblem(m, n)
            val exhaustiveSolver = new ExhaustiveSolver(pb, rule)
            val exhaustiveAlloc = exhaustiveSolver.run()
            val giftSolver = new CentralizedSolver(pb, rule, SingleGiftOnly)
            val giftAlloc = giftSolver.run()
            val swapSolver = new CentralizedSolver(pb, rule, SingleSwapAndSingleGift)
            val swapAlloc = swapSolver.run()
            val lpSolver = new LPSolver(pb, rule)
            val lpAlloc = lpSolver.run()
            exhaustiveSolverRule ::= exhaustiveAlloc.makespan()
            giftSolverRule ::= giftAlloc.makespan()
            swapSolverRule ::= swapAlloc.makespan()
            lpSolverRule ::= lpAlloc.makespan()
          }
          exhaustiveSolverRule = exhaustiveSolverRule.sortWith(_ < _)
          giftSolverRule = giftSolverRule.sortWith(_ < _)
          swapSolverRule = swapSolverRule.sortWith(_ < _)
          lpSolverRule = lpSolverRule.sortWith(_ < _)
          bw.write(
            s"$m,$n,"+
              s"${exhaustiveSolverRule.min},${exhaustiveSolverRule(nbPb/4)},${exhaustiveSolverRule(nbPb/2)},${exhaustiveSolverRule(nbPb*3/4)},${exhaustiveSolverRule.max}," +
              s"${giftSolverRule.min},${giftSolverRule(nbPb/4)},${giftSolverRule(nbPb/2)},${giftSolverRule(nbPb*3/4)},${giftSolverRule.max}," +
              s"${swapSolverRule.min},${swapSolverRule(nbPb/4)},${swapSolverRule(nbPb/2)},${swapSolverRule(nbPb*3/4)},${swapSolverRule.max}," +
              s"${lpSolverRule.min},${lpSolverRule(nbPb/4)},${lpSolverRule(nbPb/2)},${lpSolverRule(nbPb*3/4)},${lpSolverRule.max}\n")
          bw.flush()
        }
      }
    }
}
