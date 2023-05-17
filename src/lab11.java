import java.util.ArrayList;
import tester.*;

class Curriculum {
  ArrayList<Course> courses;
  Curriculum() { this.courses = new ArrayList<Course>(); }
  // EFFECT: adds another course to the set of known courses
  void addCourse(Course c) { this.courses.add(c); }
  // add methods here...
  
  //Goes through the schedule and compares to all of c's prereqs checking
  //if it is before the index of c in schedule (so before c)
  public boolean comesAfterPrereqs(ArrayList<Course> schedule, Course c) {
    int counter = 0;
    for(Course cs: schedule) {
      for(Course co: c.prereqs) {
        if(cs.equals(co) && (schedule.indexOf(cs) < schedule.indexOf(c))) {
          counter++;
        }
      }
    }
    return (counter == c.prereqs.size());
  }
  
  public boolean validSchedule(ArrayList<Course> schedule) {
    boolean result = true;
    for(Course c: schedule) {
      if(!comesAfterPrereqs(schedule, c)) {
        result = false;
      }
    }
    return result;
  }
  
}
class Course {
  String name;
  ArrayList<Course> prereqs;
  Course(String name) { this.name = name; this.prereqs = new ArrayList<Course>(); }
  // EFFECT: adds a course as a prereq to this one
  void addPrereq(Course c) { this.prereqs.add(c); }
  // add methods here
  
  public void process(ArrayList<Course> processed) {
    boolean within = false;
    ArrayList<Course> adds = new ArrayList<Course>();
    for(Course c: processed) {
      if (this.equals(c)) {
        within = true;
      }
    }
    if(within) {
      adds.add(this);
    }
  }
}

class Examples {
  Course fundies1;
  Course fundies2;
  Course database;
  Course algo;
  Course computersystems;
  Course largescale;
  Course ood;
  Course theoryOC;
  Course programming;
  Course compliers;
  Curriculum all;
  Curriculum notinorder;
  
  void initdata() {
    this.fundies1 = new Course("Fundamentals of Computer Science 1");
    this.fundies2 = new Course("Fundamentals of Computer Science 2");
    fundies2.addPrereq(fundies1);
    this.database = new Course("Database Design");
    database.addPrereq(fundies1);
    this.algo = new Course("Algorithims and Data");
    algo.addPrereq(fundies2);
    this.computersystems = new Course("Computer Systems");
    computersystems.addPrereq(fundies2);
    this.largescale = new Course("Large-Scale Parallel Data Processing");
    largescale.addPrereq(computersystems);
    largescale.addPrereq(algo);
    this.ood = new Course("Object-Oriented Design");
    ood.addPrereq(fundies2);
    this.theoryOC = new Course("Theory of Computation");
    theoryOC.addPrereq(fundies2);
    this.programming = new Course("Programming Languages");
    programming.addPrereq(theoryOC);
    programming.addPrereq(ood);
    this.compliers = new Course("Compilers");
    compliers.addPrereq(programming);
    
    this.all = new Curriculum();
    this.notinorder = new Curriculum();
    all.addCourse(fundies1);
    all.addCourse(fundies2);
    all.addCourse(database);
    all.addCourse(algo);
    all.addCourse(computersystems);
    all.addCourse(ood);
    all.addCourse(theoryOC);
    all.addCourse(largescale);
    all.addCourse(programming);
    all.addCourse(compliers);
    
    notinorder.addCourse(largescale);
    notinorder.addCourse(programming);
    notinorder.addCourse(database);
    notinorder.addCourse(fundies1);
    notinorder.addCourse(fundies2);
  }
  
  void testCAP(Tester t) {
    initdata();
    t.checkExpect(this.all.comesAfterPrereqs(this.all.courses, this.programming), true);
    t.checkExpect(this.all.comesAfterPrereqs(this.all.courses, this.largescale), true);
    t.checkExpect(this.all.comesAfterPrereqs(this.all.courses, this.compliers), true);
    t.checkExpect(this.notinorder.comesAfterPrereqs(this.notinorder.courses, this.largescale), false);
    t.checkExpect(this.notinorder.comesAfterPrereqs(this.notinorder.courses, this.programming), false);
    t.checkExpect(this.notinorder.comesAfterPrereqs(this.notinorder.courses, this.fundies2), true);
    
    t.checkExpect(this.all.validSchedule(this.all.courses), true);
    t.checkExpect(this.notinorder.validSchedule(this.notinorder.courses), false);
    
    ood.process(notinorder.courses);
    
    for(Course c: notinorder.courses) {
      System.out.println(c.name);
    }
  }
}