package pl.lukaszmarczak

/**
 * Created by lukasz on 10.04.2016.
 */
class ClusterCenterKotlin @JvmOverloads constructor(var value: Double = 0.0) {

    override fun equals(obj: Any?): Boolean {
        return obj is ClusterCenterKotlin && java.lang.Double.compare(obj.value, value) == 0
    }


    override fun hashCode(): Int {
        return (31 * java.lang.Double.doubleToLongBits(value)).toInt()
    }
}
