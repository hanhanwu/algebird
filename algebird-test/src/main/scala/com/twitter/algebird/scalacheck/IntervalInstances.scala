/*
Copyright 2016 Twitter, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.twitter.algebird
package scalacheck

import org.scalacheck.{ Arbitrary, Gen }
import org.scalacheck.Arbitrary.{ arbitrary => getArbitrary }
import Gen.oneOf

trait IntervalGen {
  def genUniverse[T: Arbitrary: Ordering]: Gen[Universe[T]] = Universe[T]()

  def genEmpty[T: Arbitrary: Ordering]: Gen[Empty[T]] = Empty[T]()

  def genInclusiveLower[T: Arbitrary: Ordering]: Gen[InclusiveLower[T]] =
    getArbitrary[T].map(InclusiveLower(_))

  def genExclusiveLower[T: Arbitrary: Ordering]: Gen[ExclusiveLower[T]] =
    getArbitrary[T].map(ExclusiveLower(_))

  def genInclusiveUpper[T: Arbitrary: Ordering]: Gen[InclusiveUpper[T]] =
    getArbitrary[T].map(InclusiveUpper(_))

  def genExclusiveUpper[T: Arbitrary: Ordering]: Gen[ExclusiveUpper[T]] =
    getArbitrary[T].map(ExclusiveUpper(_))

  def genLower[T: Arbitrary: Ordering]: Gen[Lower[T]] =
    oneOf(genInclusiveLower, genExclusiveLower)

  def genUpper[T: Arbitrary: Ordering]: Gen[Upper[T]] =
    oneOf(genInclusiveUpper, genExclusiveUpper)

  def genIntersection[T: Arbitrary: Ordering]: Gen[Interval.GenIntersection[T]] =
    for {
      l <- genLower[T]
      u <- genUpper[T] if l.intersects(u)
    } yield Intersection(l, u)
}

object IntervalGenerators extends IntervalGen

trait IntervalArb {
  import IntervalGenerators._

  implicit def intervalArb[T](implicit arb: Arbitrary[T], ord: Ordering[T]): Arbitrary[Interval[T]] =
    Arbitrary(
      oneOf(
        genUniverse[T], genEmpty[T],
        genInclusiveLower[T], genExclusiveLower[T],
        genInclusiveUpper[T], genExclusiveUpper[T],
        genIntersection[T]))

  implicit def lowerIntArb[T: Arbitrary: Ordering]: Arbitrary[Lower[T]] = Arbitrary(genLower)

  implicit def upperIntArb[T: Arbitrary: Ordering]: Arbitrary[Upper[T]] = Arbitrary(genUpper)

  implicit def intersectionArb[T: Arbitrary: Ordering]: Arbitrary[Interval.GenIntersection[T]] =
    Arbitrary(genIntersection)
}
