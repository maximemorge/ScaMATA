// Copyright (C) Maxime MORGE 2018
package org.scamata.solver

import org.scamata.core.MATA

/**
  * Abstract solver based on deals (gifts/swaps)
  * @param pb to be solver
  * @param rule
  */
abstract class DealSolver(pb : MATA,rule : SocialRule) extends Solver(pb, rule){
  var nbDeal = 0
}