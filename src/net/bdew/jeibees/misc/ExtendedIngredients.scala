/*
 * Copyright (c) bdew, 2016 - 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bdew.jeibees.misc

import mezz.jei.api.ingredients.IIngredients

import scala.reflect.ClassTag

class ExtendedIngredients(base: IIngredients) {

  import scala.collection.JavaConverters._

  private def cls[T: ClassTag] = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  def setInput[T: ClassTag](v: T) = base.setInput(cls[T], v)
  def setInputs[T: ClassTag](v: List[T]) = base.setInputs(cls[T], v.asJava)
  def setInputLists[T: ClassTag](v: List[List[T]]) = base.setInputLists(cls[T], v.map(_.asJava).asJava)
  def setOutput[T: ClassTag](v: T) = base.setOutput(cls[T], v)
  def setOutputs[T: ClassTag](v: List[T]) = base.setOutputs(cls[T], v.asJava)
  def getInputs[T: ClassTag] = base.getInputs(cls[T]).asScala.map(_.asScala)
  def getOutputs[T: ClassTag] = base.getOutputs(cls[T]).asScala
}