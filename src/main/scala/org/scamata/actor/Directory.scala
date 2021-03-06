// Copyright (C) Maxime MORGE 2018
package org.scamata.actor

import akka.actor.ActorRef
import org.scamata.core.Worker

/**
  * Class representing an index of the names and addresses of peers
  */
class Directory {
  var adr = Map[Worker, ActorRef]()//Agents' references
  var workers = Map[ActorRef, Worker]()// Actors' worker

  override def toString: String = allWorkers().mkString("[",", ","]")

  /**
    * Add to the directory
    * @param worker
    * @param ref
    */
  def add(worker: Worker, ref: ActorRef) : Unit = {
    if ( ! adr.keySet.contains(worker) &&  ! workers.keySet.contains(ref)) {
      adr += (worker -> ref)
      workers += (ref -> worker)
    }
    else throw new RuntimeException(s"$worker and/or $ref already in the directory")
  }

  def allActors() : Iterable[ActorRef]  = adr.values
  def allWorkers() : Iterable[Worker]  = workers.values
  def peers(worker: Worker) : Set[Worker] = allWorkers().filterNot(_ ==worker).toSet
  def peersActor(worker: Worker) :  Iterable[ActorRef] = peers(worker: Worker).map(w => adr(w))

}
