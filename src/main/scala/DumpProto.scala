package tutorial

import com.example.protos.demo._

import java.io.{FileNotFoundException, FileInputStream, FileOutputStream}
import scala.util.Try
import scala.io.StdIn
import scala.util.Using

object DumpProto extends App {

    def readFromFile(path: String): Person = 
        // input streamのバイナリをパースする
        Using(new FileInputStream(path)) { fileInputStream => 
            Person.parseFrom(fileInputStream)
        }.recover {
            case _: FileNotFoundException =>
                println("No person found. Will create a new file.")
                Person()
        }.get

    def addPerson(path: String): Unit = {
        val newPerson = Person(
            name = Some("Alice"),
            age = Some(30),
            gender = Some(Gender.MALE),
            addresses = Seq(
                Address(street = Some("1st"), city = Some("NewYork"))
            )
        ) 
        Using(new FileOutputStream(path)) { output =>
            newPerson.writeTo(output)
        }   
    }

    def menu(): Int = {
        println("What would you like to do today?")
        println("1. Show the address book")
        println("2. Add new person to address book.")
        println("3. Exit")
        println()
        println("Enter your choice [1, 2, or 3] and hit Enter: ")
        val in = Try(StdIn.readInt()).toOption.getOrElse(-1)
        if (in != 1 && in != 2 && in != 3) {
          println("Invalid response.")
          menu()
        } else in
    }
    
    val filePath = "data/demo_person.pb"

    menu() match {
        case 1 => 
            val person = readFromFile(filePath)
            println(person.toProtoString)
        case 2 =>
            addPerson(filePath)
        case _ =>
    }
}