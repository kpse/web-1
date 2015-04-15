package controllers.V2

import controllers.Secured
import models.{School, Children, Employee, Parent}
import models.Parent.writeParent
import models.Employee.writeEmployee
import models.Children.writeChildInfo
import models.School.schoolClassWriter
import play.api.libs.json.Json
import play.api.mvc.Controller

object RemoveListController extends Controller with Secured {

  def parents(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(Parent.removed(kg)))
  }

  def employees(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(Employee.removed(kg)))
  }

  def children(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(Children.removed(kg)))
  }

  def classes(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(School.removedClasses(kg)))
  }
}
