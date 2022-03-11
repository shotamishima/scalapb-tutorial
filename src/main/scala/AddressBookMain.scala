package tutorial

import tutorial.addressbook.{AddressBook, Person}

import java.io.{FileNotFoundException, FileInputStream, FileOutputStream}
import scala.util.Try
import scala.io.StdIn
import scala.util.Using

object AddressBookMain extends App {

  def readFromFile(): AddressBook = 
    Using(new FileInputStream("data/addressbook.pb")) { fileInputStream => 
      AddressBook.parseFrom(fileInputStream)
    }.recover {
      case _: FileNotFoundException =>
        println("No address book found. Will create a new file.")
        AddressBook()
    }.get

  def personFromStdin(): Person = {
    print("Enter person ID (int): ")
    val id = StdIn.readInt()
    print("Enter name: ")
    val name = StdIn.readLine()
    print("Enter email address (blank for none): ")
    val email = StdIn.readLine()

    def getPhone(): Option[Person.PhoneNumber] = {
      print("Enter a phone number (or leave blank to finish): ")
      val number = StdIn.readLine()
      if (number.nonEmpty) {
        print("Is this a mobile, home, or work phone [mobile, home, work] ? ")
        val typ = StdIn.readLine() match {
          case "mobile" => Some(Person.PhoneType.MOBILE)
          case "home" => Some(Person.PhoneType.HOME)
          case "work" => Some(Person.PhoneType.WORK)
          case _ => 
            println("Unknown phone type. Leaving as None")
            None
        }
        Some(Person.PhoneNumber(number = number, `type` = typ))
      } else None
    }

    // keep prompting for phone numbers until None is returned.
    val phones = 
      Iterator
        .continually(getPhone())
        .takeWhile(_.nonEmpty)
        .flatten
        .toSeq

    Person(
      id = id,
      name = name,
      email = if (email.nonEmpty) Some(email) else None,
      phones = phones
    )
  }

  def addPerson(): Unit = {
    val newPerson = personFromStdin()
    val addressBook = readFromFile()
    val updated = addressBook.update(
      _.people :+= newPerson
    )
    Using(new FileOutputStream("data/addressbook.pb")) { output => 
      updated.writeTo(output)
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

  menu() match {
    case 1 =>
      // Reads the existing address book
      val addressBook = readFromFile()
      println(addressBook.toProtoString)
    case 2 => 
      addPerson()
    case _ =>
  }
}
