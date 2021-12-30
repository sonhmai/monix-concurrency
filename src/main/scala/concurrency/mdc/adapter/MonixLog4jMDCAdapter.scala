package concurrency.mdc.adapter

import monix.execution.misc.Local
import org.apache.logging.log4j.spi.ThreadContextMap
import org.slf4j.MDC

import java.{util => ju}

// referenced from https://github.com/mdedetrich/monix-mdc/blob/master/src/main/scala/org/mdedetrich/monix/mdc/MonixMDCAdapter.scala

class MonixLog4jMDCAdapter extends ThreadContextMap {
  private[this] val map = Local[ju.Map[String, String]](ju.Collections.emptyMap())

  override def put(key: String, `val`: String): Unit = {
    if (map() eq ju.Collections.EMPTY_MAP) {
      map := new ju.HashMap()
    }
    map().put(key, `val`)
    ()
  }

  override def get(key: String): String = map().get(key)
  override def remove(key: String): Unit = {
    map().remove(key)
    ()
  }

  // Note: we're resetting the Local to default, not clearing the actual hashmap
  override def clear(): Unit                               = map.clear()
  override def getCopy: ju.Map[String, String] = new ju.HashMap(map())

  override def getImmutableMapOrNull: ju.Map[String, String] = {
    if (isEmpty) {
      null
    } else {
      getCopy
    }
  }
  override def containsKey(key: String): Boolean =
    map().containsKey(key)

  override def isEmpty: Boolean = map().isEmpty
}

object MonixLog4jMDCAdapter {

  /** Initializes the [[MonixLog4jMDCAdapter]] by overriding the default MDCAdaptor. Typically
   * you would call this once in your Main (or equivalent).
   *
   * NOTE: This will override the default MDCAdaptor which means that MDC will no longer
   * propagate via [[ThreadLocal]]
   */
  def initialize(): Unit = {

    val field = classOf[MDC].getDeclaredField("mdcAdapter")
    field.setAccessible(true)
    field.set(null, new MonixLog4jMDCAdapter)
    field.setAccessible(false)
  }
}
