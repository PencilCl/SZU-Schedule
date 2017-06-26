SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachmentName` varchar(255) NOT NULL,
  `attachmentUrl` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of attachment
-- ----------------------------

-- ----------------------------
-- Table structure for blackboard
-- ----------------------------
DROP TABLE IF EXISTS `blackboard`;
CREATE TABLE `blackboard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subjectID` int(11) NOT NULL,
  `studentID` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `student_bb` (`studentID`),
  KEY `subject_bb` (`subjectID`),
  CONSTRAINT `student_bb` FOREIGN KEY (`studentID`) REFERENCES `student` (`id`),
  CONSTRAINT `subject_bb` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of blackboard
-- ----------------------------

-- ----------------------------
-- Table structure for homework
-- ----------------------------
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `homeworkName` varchar(255) NOT NULL,
  `discription` varchar(255) NOT NULL,
  `subjectID` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `deadline` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `homework_subject` (`subjectID`),
  CONSTRAINT `homework_ibfk_1` FOREIGN KEY (`subjectID`) REFERENCES `subject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of homework
-- ----------------------------

-- ----------------------------
-- Table structure for lesson
-- ----------------------------
DROP TABLE IF EXISTS `lesson`;
CREATE TABLE `lesson` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lessonName` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `day` int(11) NOT NULL,
  `begin` int(11) NOT NULL,
  `end` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of lesson
-- ----------------------------

-- ----------------------------
-- Table structure for library
-- ----------------------------
DROP TABLE IF EXISTS `library`;
CREATE TABLE `library` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `studentID` int(25) NOT NULL,
  `bookName` varchar(255) DEFAULT NULL,
  `startDate` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `endDate` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_library` (`studentID`),
  CONSTRAINT `student_library` FOREIGN KEY (`studentID`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of library
-- ----------------------------

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `id` int(11) NOT NULL,
  `studentID` int(11) NOT NULL,
  `lessonID` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `student_schedule` (`studentID`),
  KEY `schedule_lesson` (`lessonID`),
  CONSTRAINT `schedule_lesson` FOREIGN KEY (`lessonID`) REFERENCES `lesson` (`id`),
  CONSTRAINT `student_schedule` FOREIGN KEY (`studentID`) REFERENCES `student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of schedule
-- ----------------------------

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `sex` char(2) NOT NULL,
  `stuNum` varchar(50) NOT NULL,
  `account` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of student
-- ----------------------------

-- ----------------------------
-- Table structure for subject
-- ----------------------------
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subjectName` varchar(255) NOT NULL,
  `homeworkID` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `subject_homework` (`homeworkID`),
  CONSTRAINT `subject_homework` FOREIGN KEY (`homeworkID`) REFERENCES `subjecthomework` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of subject
-- ----------------------------

-- ----------------------------
-- Table structure for subjecthomework
-- ----------------------------
DROP TABLE IF EXISTS `subjecthomework`;
CREATE TABLE `subjecthomework` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `homeworkID` int(11) NOT NULL,
  `attachmentID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sub_hw_hw` (`homeworkID`),
  KEY `sub_hw_attach` (`attachmentID`),
  CONSTRAINT `sub_hw_attach` FOREIGN KEY (`attachmentID`) REFERENCES `attachment` (`id`),
  CONSTRAINT `sub_hw_hw` FOREIGN KEY (`homeworkID`) REFERENCES `homework` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of subjecthomework
-- ----------------------------
