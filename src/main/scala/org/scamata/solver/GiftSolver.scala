// Copyright (C) Maxime MORGE 2018
package org.scamata.solver

import org.scamata.core._
import org.scamata.deal._

import scala.collection.{SortedSet, immutable}
import scala.util.Random

/**
  * Multiagent negotiation process for minimizing the rule
  * @param pb to be solver
  * @param rule to be optimized
  */
class GiftSolver(pb : MWTA, rule : SocialRule) extends DealSolver(pb, rule) {
  debug = false
  val trace = false

  /**
    * Returns an allocation
    */
  override def solve(): Allocation = {
    val allocation = Allocation.randomAllocation(pb)
    if (debug) println(s"Give with a random allocation:\n$allocation")
    reallocate(allocation)
  }

    /**
    * Reallocate
    */
  def reallocate(initialAllocation: Allocation): Allocation = {
    var allocation = initialAllocation
    var activeWorkers: List[Worker] = Random.shuffle(pb.workers.toList)
    if (debug) println("All peers are initially active")
    while(activeWorkers.nonEmpty){
      activeWorkers.foreach { initiator: Worker =>
        if (debug) println(s"$initiator tries to find a social rational gift")
        val potentialPartners = rule match {
          case Flowtime => // all the peers
            pb.workers.filterNot(_ == initiator)
          case Cmax => // the peers with a smallest workload
            pb.workers.filterNot(_ == initiator).filter(allocation.workload(_) < allocation.workload(initiator))
        }
        if (debug) println(s"Potential partner: $potentialPartners")
        if (potentialPartners.isEmpty || allocation.bundle(initiator).isEmpty) {
          activeWorkers = activeWorkers.filter(_ != initiator)
          if (debug) println(s"$initiator becomes inactive")
        }
        else {
          var found = false
          var bestAllocation: Allocation = allocation
          var bestSingleGift: Gift = new Gift(initiator, initiator, Set[Task]())
          var bestGoal = rule match {
            case Cmax => allocation.workload(initiator)//allocation.makespan()
            case Flowtime => allocation.flowtime()
          }
          potentialPartners.foreach { opponent =>
            allocation.bundle(initiator).foreach { task =>
              // 4 - Foreach potential swap
              val gift = new SingleGift(initiator, opponent, task)
              val modifiedAllocation = allocation.gift(gift)
              val currentGoal = rule match { // Compute the new goal
              case Cmax => Math.max(modifiedAllocation.workload(initiator), modifiedAllocation.workload(opponent))//modifiedAllocation.makespan()
              case Flowtime => modifiedAllocation.flowtime()
            }
              if (currentGoal < bestGoal) {
                bestGoal = currentGoal
                bestAllocation = modifiedAllocation
                bestSingleGift = gift
                found = true
              }
            }
          }
          // Select the best gift if any
          if (!found) {
            if (debug) println(s"$initiator becomes inactive")
            activeWorkers = activeWorkers.filter(_ != initiator)
          } else {
            if (debug || trace) println(s"$bestSingleGift")
            nbConfirm += 1
            allocation = bestAllocation
            if (rule == Cmax && ! activeWorkers.contains(bestSingleGift.supplier)) {
              activeWorkers = bestSingleGift.supplier :: activeWorkers
            }
            if (rule == Cmax) {
              pb.workers.filter(worker => allocation.workload(worker) > bestGoal &&  ! activeWorkers.contains(worker)).foreach { worker =>
                activeWorkers = worker :: activeWorkers
              }
              activeWorkers = Random.shuffle(activeWorkers)
            }
          }
        }
      }
    }
    allocation
  }
}

/**
  * Companion object to test it
  */
object GiftSolver extends App {
  val debug = false
  import org.scamata.example.toy4x4._
  println(pb)
  val negotiationSolver = new GiftSolver(pb,Cmax)
  var allocation = new Allocation(pb)
  allocation = allocation.update(a1, SortedSet(t4))
  allocation = allocation.update(a2, SortedSet(t3))
  allocation = allocation.update(a3, SortedSet(t1))
  allocation = allocation.update(a4, SortedSet(t2))
  println(allocation)
  println(negotiationSolver.reallocate(allocation).toString)

}