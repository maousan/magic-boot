{
  "properties" : { },
  "id" : "12d3dc8175094104b912fd5bc7209cf2",
  "script" : null,
  "groupId" : "14a38d7ee90341f39be1ff09dd2bff0f",
  "name" : "groovy1",
  "createTime" : 1772629333800,
  "updateTime" : 1772675835416,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/demo1",
  "enabled" : false,
  "nodeId" : "groovy1",
  "namespace" : null,
  "route" : null,
  "description" : "",
  "scriptType" : "groovy",
  "nodeType" : "common"
}
================================
// LiteFlow Groovy 脚本
import cn.hutool.core.collection.ListUtil
import cn.hutool.core.date.DateUtil

import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

def date = DateUtil.parse("2022-10-17 13:31:43")
println(date)
defaultContext.setData("demoDate", date)

List<String> list = ListUtil.toList("a", "b", "c")

List<String> resultList = list.stream().map(s -> "hello," + s).collect(Collectors.toList())

defaultContext.setData("resultList", resultList)

class Student {
    int studentID
    String studentName
}

Student student = new Student()
student.studentID = 100301
student.studentName = "张三"
defaultContext.setData("student", student)

def a = 3
def b = 2
defaultContext.setData("s1", a * b)