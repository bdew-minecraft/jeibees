package net.bdew.jeibees

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.{ExtensionPoint, ModLoadingContext}
import org.apache.commons.lang3.tuple.Pair
import org.apache.logging.log4j.{LogManager, Logger}

import java.util.function.{BiPredicate, Supplier}

@Mod(JEIBees.ModId)
object JEIBees {
  final val ModId = "jeibees"
  val log: Logger = LogManager.getLogger

  def logDebug(msg: String, args: Any*): Unit = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*): Unit = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*): Unit = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*): Unit = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*): Unit = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*): Unit = log.error(msg.format(args: _*), t)

  Config.init()

  ModLoadingContext.get.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () =>
    Pair.of(() => "ANY", (remote, isServer) => true): Pair[Supplier[String], BiPredicate[String, java.lang.Boolean]])
}
