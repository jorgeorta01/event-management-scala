package controllers

import javax.inject._
import play.api._
import play.api.mvc._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
   
   val courses: Array[Map[String, Any]] = Array( 
        Map("name" -> "1370", "sections" -> Array(Map("id" -> 1, "instructor" -> 12), Map("id" -> 2, "instructor" -> 2))),
        Map("name" -> "3329", "sections" -> Array(Map("id" -> 3, "instructor" -> 7), Map("id" -> 4, "instructor" -> 31), Map("id" -> 5, "instructor" -> 31))),
        Map("name" -> "3340", "sections" -> Array(Map("id" -> 6, "instructor" -> 23), Map("id" -> 7, "instructor" -> 3)))
    )

    val instructors: Array[Map[String, Any]] = Array(
        Map("id" -> 12, "name" -> "Ayati"),
        Map("id" -> 7, "name" -> "Gao"),
        Map("id" -> 2, "name" -> "Kim"),
        Map("id" -> 23, "name" -> "Schweller"),
        Map("id" -> 31, "name" -> "Tomai"),
        Map("id" -> 3, "name" -> "Wylie")
    )

    Ok(views.html.index(courses, instructors))
  }
}
