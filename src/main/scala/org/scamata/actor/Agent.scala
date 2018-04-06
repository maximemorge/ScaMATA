// Copyright (C) Maxime MORGE 2018
package org.scamata.actor

import org.scamata.core.{Task, Worker}
import akka.actor.{Actor, FSM}
import org.scamata.deal.{Gift, SingleGift}
import org.scamata.solver.{Cmax, Flowtime, SocialRule}

import scala.collection.SortedSet

/**
  * States of the agent
  */
sealed trait State
case object Pause extends State
case object Proposer extends State

/**
  * Internal immutable state of mind
  * @param bundle
  * @param workers
  * @param belief about the workloads
  */
class StateOfMind(val bundle: SortedSet[Task], val workers: Iterable[Worker], val belief: Map[Worker, Double])
  extends Tuple3[SortedSet[Task], Iterable[Worker], Map[Worker, Double]](bundle, workers, belief)


/**
  * Abstract class representing an agent
  * @param worker which is embedded
  * @param rule to optimize
  */
abstract class Agent(val worker: Worker, val rule: SocialRule) extends Actor{
  val debug=false

  var supervisor = context.parent
  var directory = new Directory()
  var cost = Map[(Worker, Task), Double]()

  /**
    * Broadcasts workload
    */
  def broadcastInform(workload: Double) : Unit = {
    directory.allActors().foreach(_ ! Inform(worker, workload))
  }

  /**
    * Handles management messages
    * @param message
    */
  def defaultReceive(message : Message) : Any = message match {
    case Initiate(d, c) => // Initiate the directory and the cost matrix
      this.directory = d
      this.cost = c
      if (debug) println(s"$worker Cost Matrix:\n $cost")
    case Stop => context.stop(self) // Stop the actor
  }

  /**
    * Returns true if a task can be delegated
    * @param task to delegate
    * @param provider
    * @param supplier
    * @param belief about the workload
    */
  def acceptable(task : Task, provider : Worker, supplier : Worker, belief:  Map[Worker, Double]) : Boolean = {
    rule match {
      case Cmax => // The local Cmax must strictly decrease
        Math.max( belief(provider), belief(supplier)) > Math.max( belief(provider)-cost(provider, task),  belief(supplier) + cost(supplier, task))
      case Flowtime => // The local flowtime  must strictly decrease
        cost(provider, task) > cost(supplier, task)
    }
  }


}

