package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.db.Database
import scala.collection.mutable.ArrayBuffer

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
 
case class Course(id: Int, name: String, sections: Seq[Section])
case class Section(id: Int, instructorId: Int)


@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, db: Database) extends BaseController {
    val instructors: Array[Map[String, Any]] = Array(
      Map("id" -> 12, "name" -> "Ayati"),
      Map("id" -> 7, "name" -> "Gao"),
      Map("id" -> 2, "name" -> "Kim"),
      Map("id" -> 23, "name" -> "Schweller"),
      Map("id" -> 31, "name" -> "Tomai"),
      Map("id" -> 3, "name" -> "Wylie")
  )
  
  def index() = Action { implicit request: Request[AnyContent] =>
   
    val courses1 = getCourses(db)
    println(courses1)

    Ok(views.html.index(courses1, instructors))
  }

 // Method to retrieve courses with their associated sections from the database

private def getCourses(db: Database): Seq[Course] = {
  db.withConnection { conn =>
    val query = "SELECT * FROM course"
    val stmt = conn.createStatement()
    val rs = stmt.executeQuery(query)
    
    val courses = ArrayBuffer[Course]() // Use ArrayBuffer for mutable collection

    while (rs.next()) {
      val courseId = rs.getInt("id")
      val courseName = rs.getString("name")
      val sections = getSectionsForCourse(courseId, db)

      val course = Course(courseId, courseName, sections)
      courses += course // Append to ArrayBuffer
    }

    courses.toSeq // Convert back to immutable Seq for return type
  }
}

  // Method to retrieve sections associated with passed course
  private def getSectionsForCourse(courseId: Int, db: Database): Seq[Section] = {
    db.withConnection { conn =>
      val query = "SELECT * FROM section WHERE course_id = ?"
      val pstmt = conn.prepareStatement(query)
      pstmt.setInt(1, courseId)

      val rs = pstmt.executeQuery()

      val sections = ArrayBuffer[Section]()

      while (rs.next()) {
        val sectionId = rs.getInt("id")
        val instructorId = rs.getInt("instructor_id")
        val section = Section(sectionId, instructorId)
        sections += section
      }

      sections.toSeq
    }
  }
}