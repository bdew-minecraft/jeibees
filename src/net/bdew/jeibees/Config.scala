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

package net.bdew.jeibees

import java.io.File

import forestry.api.genetics.{AlleleManager, ISpeciesRoot}
import forestry.api.lepidopterology.IButterflyRoot
import net.minecraftforge.common.config.Configuration

import scala.collection.JavaConversions._

object Config {
  var showRequirements = true
  var showSecret = true
  var identifyGenome = true
  var rootConfigs = Map.empty[ISpeciesRoot, RootConfig]

  val disabledConfig = RootConfig(false, false)

  case class RootConfig(showMutations: Boolean, showProduce: Boolean)

  def load(file: File): Unit = {
    val cfg = new Configuration(file)
    try {

      showRequirements = cfg.get("All", "ShowRequirements", true, "Set to false to disable display of mutation requirements").getBoolean
      showSecret = cfg.get("All", "ShowSecret", true, "Set to false to disable display of secret mutations").getBoolean
      identifyGenome = cfg.get("All", "IdentifyGenome", true, "Set to false to disable showing identified genome in recipes").getBoolean

      for ((id, root) <- AlleleManager.alleleRegistry.getSpeciesRoot) {
        val name = id.replace("root", "").capitalize

        val showMutations = cfg.get(name, "ShowMutations", true, "Set to false to disable display of mutations").getBoolean

        val showProduce =
          if (!root.isInstanceOf[IButterflyRoot])
            cfg.get(name, "ShowProduce", true, "Set to false to disable display of produce").getBoolean
          else
            false

        rootConfigs += root -> RootConfig(showMutations, showProduce)
      }

    } finally {
      cfg.save()
    }
  }
}
