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
      Map("id" -> 1, "name" -> "Figueroa"),
      Map("id" -> 12, "name" -> "Ayati"),
      Map("id" -> 7, "name" -> "Gao"),
      Map("id" -> 2, "name" -> "Kim"),
      Map("id" -> 23, "name" -> "Schweller"),
      Map("id" -> 31, "name" -> "Tomai"),
      Map("id" -> 3, "name" -> "Wylie")
  )
  
  def index() = Action { implicit request: Request[AnyContent] =>
   
    val courses = getCourses(db)

    Ok(views.html.index(courses, instructors))
  }

  def addInstructor() = Action { request =>
    // Extract instructor ID from form data
    val instructorIdOpt = request.body.asFormUrlEncoded.flatMap(_.get("instructorId").flatMap(_.headOption))
    
    // Extract course ID from form data
    val courseIdOpt = request.body.asFormUrlEncoded.flatMap(_.get("courseId").flatMap(_.headOption))

    // If both instructor ID and course ID are present, proceed
    (instructorIdOpt, courseIdOpt) match {
        case (Some(instructorId), Some(courseId)) =>
            val instructorIdInt = instructorId.toInt
            val courseIdInt = courseId.toInt
            
            // Call addSection function with both IDs
            addSection(db, instructorIdInt, courseIdInt)

            val courses = getCourses(db)
            println("Added instructor to section")

            Ok(views.html.index(courses, instructors))
        
        case _ =>
            // Handle the case where either instructor ID or course ID is missing
            BadRequest("Missing instructor ID or course ID")
    }
  }

  def addCourse() = Action { request =>

    // Extract course ID from form data
    val newCourse = request.body.asFormUrlEncoded.flatMap(_.get("addCourse").flatMap(_.headOption))

    // If both instructor ID and course ID are present, proceed
    (newCourse) match {
        case (Some(courseId)) =>
   
            // Call addSection function with both IDs
            addCourseToDatabase(courseId)

            val courses = getCourses(db)
            println("Added instructor to section")

            Ok(views.html.index(courses, instructors))
        
        case _ =>
            // Handle the case where either instructor ID or course ID is missing
            BadRequest("Missing instructor ID or course ID")
    }
  }

  def changeSection() = Action { request =>
      val sectionIdFromForm = request.body.asFormUrlEncoded.flatMap(_.get("id").flatMap(_.headOption)).map(_.toInt)

      val instructorIdOpt = request.body.asFormUrlEncoded.flatMap(_.get("instructorId").flatMap(_.headOption))

          (instructorIdOpt, sectionIdFromForm) match {
          case (Some(iId), Some(sId)) =>
              val instructorIdInt = iId.toInt
              val sectionIdInt = sId.toInt
              
              changeSectionFromDatabase(sectionIdInt, instructorIdInt)

              val courses = getCourses(db)
              println("Added instructor to section")

              Ok(views.html.index(courses, instructors))
          
          case _ =>
              // Handle the case where either instructor ID or course ID is missing
              BadRequest("Missing instructor ID or course ID")
      }
  }


  def deleteSection() = Action { request =>
    val sectionIdFromForm = request.body.asFormUrlEncoded.flatMap(_.get("id").flatMap(_.headOption)).map(_.toInt)
    sectionIdFromForm.foreach { id =>
      deleteSectionFromDatabase(id)
    }

  
    val courses = getCourses(db)

    Ok(views.html.index(courses, instructors))
  }
    

  private def changeSectionFromDatabase(sectionId: Int, instructorId: Int): Unit = {
    db.withConnection { conn =>
      val updateStatement = conn.prepareStatement(s"UPDATE section SET instructor_id = $instructorId WHERE id = $sectionId")
      updateStatement.executeUpdate()
    }
  }
    
  // Method to delete a section from the database based on its ID
  private def deleteSectionFromDatabase(sectionId: Int): Unit = {
    db.withConnection { conn =>
      val deleteStatement = conn.prepareStatement(s"DELETE FROM section WHERE id = $sectionId")
      deleteStatement.executeUpdate()
    }
  }
    
  
 // Method to retrieve courses with their associated sections from the database
  
  private def addSection(db: Database, Iid: Int, Cid: Int): Unit = {
    db.withConnection { conn =>
      val query = s"INSERT INTO section (course_id, instructor_id) VALUES ($Cid, $Iid)"
      val stmt = conn.createStatement()
      stmt.executeUpdate(query)
    }
  }

  private def addCourseToDatabase(cId: String): Unit = {
    db.withConnection { conn =>
      val query = s"INSERT INTO course (name) VALUES ($cId)"
      val stmt = conn.createStatement()
      stmt.executeUpdate(query)
    }
  }

  def getName(id: Int): String = {
    instructors.find(_.get("id").contains(id)) match {
      case Some(instructor) => instructor("name").toString
      case None => "ID not found"
    }
  }

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