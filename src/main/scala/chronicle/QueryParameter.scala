package chronicle

case class QueryParameter[T](paramName: String, value: T) {
  override def toString = s"$paramName=$value"
}
