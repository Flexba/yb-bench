import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

var isStopped = false

fun main(args: Array<String>) {
    println("Starting Benchmark!")

    val threads = mutableSetOf<Thread>()

    for (i in 1..10) {
        println("Starting Thread $i")

        val thread = thread(start = true) {
            val conn = getConnection(i)
            createTables(conn, i)
            load(i, conn)
        }
        threads.add(thread)
    }

    println("Benchmark is fully running. Press enter to exit")
    readLine()

    isStopped = true
    for (thread in threads) {
        thread.join()
    }

    println("Benchmark stopped!")
}

fun getConnection(i: Int): Connection {
    for (j in 1..5) {
        try {
            val url = "jdbc:postgresql://localhost:5433/postgres"
            val props = Properties()
            props.setProperty("user", "yugabyte")
            props.setProperty("password", "yugabyte")
            props.setProperty("ssl", "false")
            val conn = DriverManager.getConnection(url, props)

            println("Received connection for Benchmark $i")
            return conn
        } catch (e: Exception) {
            println("Exception for Benchmark $i")
        }
    }

    println("Benchmark $i could not get a connection")
    throw RuntimeException("Benchmark $i could not get a connection")
}

fun load(i: Int, conn: Connection = getConnection(i)) {
    var cyclesOk = 0
    var cyclesFailed = 0
    val times = mutableListOf<Long>()

    while (!isStopped) {
        println("Benchmark $i has $cyclesOk cycles OK, last took ${times.lastOrNull()}")

        try {
            times.add(measureTimeMillis {
                delete(conn, i)
                insert(conn, i)
                select(conn, i)
            })

            cyclesOk++
        } catch (e: Exception) {
            print("-- In number $i: ")
            e.printStackTrace()
            cyclesFailed++
        }
    }

    conn.close()

    println(
        """Benchmark $i had
        |  $cyclesOk OK cycles
        |  $cyclesFailed failures
        |  Total time: ${times.sum()}ms
        |  Average time: ${times.average()}ms
        |  Deviation: ${calculateSD(times)}ms""".trimMargin()
    )
}

fun calculateSD(times: Collection<Long>): Double {
    val mean = times.average();
    return times
        .fold(0.0) { accumulator, next -> accumulator + (next - mean).pow(2.0) }
        .let { sqrt(it / times.size) }
}

fun createTables(conn: Connection, i: Int) {
    with(conn.createStatement()) {
        execute("DROP TABLE IF EXISTS persons$i CASCADE;")
        close()
    }
    with(conn.createStatement()) {
        execute(
            """
        CREATE TABLE persons$i (
            person_id INT,
            name VARCHAR,
            PRIMARY KEY(person_id)
        ) 
        """
        )
        close()
    }

    with(conn.createStatement()) {
        execute("DROP TABLE IF EXISTS orders$i CASCADE;")
        close()
    }
    with(conn.createStatement()) {
        execute(
            """
        CREATE TABLE orders$i (
            id INT,
            person_id INT NOT NULL,
            products VARCHAR,
            PRIMARY KEY (id, person_id),
            CONSTRAINT fk_person FOREIGN KEY(person_id) REFERENCES persons$i(person_id)
        ) 
        """
        )
        close()
    }
}

fun insert(conn: Connection, i: Int) {
//    conn.autoCommit = false;
    with(conn.createStatement()) {
        execute("INSERT INTO persons$i VALUES " + (1..100).map { "($it, 'Person $it')" }.joinToString(","))
        close()
    }
    with(conn.createStatement()) {
        execute("INSERT INTO orders$i VALUES " + (1..5).flatMap { orderId -> (1..100).map { personId -> "($orderId, $personId, 'Order $orderId')" } }
            .joinToString(","))
        close()
    }
//    conn.commit()
//    conn.autoCommit = true;
}

fun delete(conn: Connection, i: Int) {
//    conn.autoCommit = false;
    with(conn.createStatement()) {
        execute("DELETE FROM orders$i")
        close()
    }
    with(conn.createStatement()) {
        execute("DELETE FROM persons$i")
        close()
    }
//    conn.commit()
//    conn.autoCommit = true;
}

fun select(conn: Connection, i: Int) {
    with(conn.createStatement()) {
        val rs: ResultSet = executeQuery("SELECT * FROM persons$i WHERE name = 'Person 3'")
        if (!rs.next()) {
            println("No results from statement 1 in $i")
        }
        while (!rs.isLast) {
            rs.next()
        }
        rs.close()
        close()
    }

    with(conn.createStatement()) {
        val rs: ResultSet = executeQuery("SELECT * FROM orders$i JOIN persons$i USING (person_id)")
        if (!rs.next()) {
            println("No results from statement 2 in $i")
        }
        while (!rs.isLast) {
            rs.next()
        }
        rs.close()
        close()
    }
}