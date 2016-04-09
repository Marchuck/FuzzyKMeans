package pl.lukaszmarczak

import java.util.ArrayList

/**
 * Created by lukasz on 09.04.2016.
 */
class FuzzyKMeansAlgorithmKotlinned private constructor() {

    fun isValid(u: Array<DoubleArray>): Boolean {
        return Math.ceil(sumU(u)) == 1.0
    }

    fun sumU(u: Array<DoubleArray>): Double {
        var size = 0.0
        for (anU in u)
            for (anAnU in anU) size += anAnU
        return size
    }

    fun multiplyUwithX(u: Array<DoubleArray>, X: DoubleArray): Double {
        var size = 0.0
        for (i in u.indices)
            for (uRow in u[i]) size += uRow * X[i]
        return size
    }

    fun runAlgorithm() {
        //step 1
        val clusterCenters = ArrayList<ClusterCenterKotlin>()
        clusterCenters.add(ClusterCenterKotlin())
        val p = 1
        val m = 2
        val u = createDummyU()
        val X = createDummyU()[1]
        val d = createDummyU()
        var previousCluster = clusterCenters[0]
        var currentCluster: ClusterCenterKotlin? = null

        //step 2
        while (!clustersMetricStopCondition(currentCluster, previousCluster)) {
            for (j in u.indices) {
                for (i in 0..u[j].size - 1) {
                    if (isDTooSmall(d[j][i])) {
                        u[j][i] = 1.0
                    } else {
                        val fixedDPower = (1 / (m - 1)).toDouble()
                        val dPowered = Math.pow(d[j][i], fixedDPower)
                        val sumPowered = calculateSumPowered(d[i], fixedDPower)
                        val result = 1 / (dPowered * sumPowered)
                        u[j][i] = result
                    }
                }
            }
            //step 3
            clusterCenters.add(calculateNewCluster(u, X, m))
            val currentClusterSize = clusterCenters.size
            if (currentClusterSize > 1) {
                previousCluster = clusterCenters[currentClusterSize - 2]
                currentCluster = clusterCenters[currentClusterSize - 1]
            }
        }
        for (c in clusterCenters) {
            log("Next cluster: " + c.value)
        }
    }

    private fun clustersMetricStopCondition(c1: ClusterCenterKotlin?, c2: ClusterCenterKotlin?): Boolean {
        if (c1 == null || c2 == null) return false
        var value = c1.value - c2.value
        if (value < 0) value = -value
        return value < verySmallPositiveNumber
    }


    private fun calculateNewCluster(u: Array<DoubleArray>, X: DoubleArray, m: Int): ClusterCenterKotlin {
        val a = multiplyUwithX(u, X)
        val b = sumU(u)
        val value = a / b
        return ClusterCenterKotlin(value)
    }

    private fun isDTooSmall(d: Double): Boolean {
        return d < verySmallPositiveNumber
    }

    private fun calculateSumPowered(di: DoubleArray, m: Double): Double {
        var sum = 0.0
        for (aDi in di) sum += Math.pow(1 / aDi, m)
        return sum
    }

    fun calculateCoefficientJ(u: Array<DoubleArray>, d: Array<DoubleArray>): Double {
        if (!isValid(u)) throw IllegalArgumentException("Invalid argument")
        //        if (u.length!=d.length) throw new IllegalArgumentException("Given u = "+u.length+" got d = "+d.length);
        var J = 0.0
        for (j in u.indices) {
            for (i in d.indices) {
                val result = u[j][i] * d[j][i]
                J += result
            }
        }
        return J
    }

    fun createDummyU(): Array<DoubleArray> {
        val factor = (1 / 6f).toDouble()
        val u = arrayOf(doubleArrayOf(factor, factor, 0.0),
                        doubleArrayOf(0.0, 2 * factor, 0.0),
                        doubleArrayOf(factor, 0.0, factor))
        return u
    }

    fun createEmpty(capacity: Int): Array<IntArray> {
        val matrix = Array(capacity) { IntArray(capacity) }
        return matrix
    }

    fun fillRows(vararg rows: IntArray): Array<IntArray> {
        val actualLength = rows[0].size
        for (j in rows.indices) {
            val nextLength = rows[j].size
            if (nextLength != actualLength)
                throw IllegalArgumentException("Invalid argument for $actualLength got $nextLength")
        }
        val out = Array(actualLength) { IntArray(rows.size) }
        for (i in rows.indices) {
            System.arraycopy(rows[i], 0, out[i], 0, rows[i].size)
        }
        return out
    }

    companion object {

        val verySmallPositiveNumber = 1 / Math.pow(10.0, 10.0)


        @Synchronized fun create(): FuzzyKMeansAlgorithmKotlinned {
            return FuzzyKMeansAlgorithmKotlinned()
        }

        fun log(s: String) {
            println(s)
        }

        @JvmStatic fun main(args: Array<String>) {
            val fkm = FuzzyKMeansAlgorithmKotlinned()
            fkm.runAlgorithm()
        }
    }
}
