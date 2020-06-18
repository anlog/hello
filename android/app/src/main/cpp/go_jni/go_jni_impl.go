package main

// #cgo LDFLAGS: -llog
// #include <android/log.h>
import "C"

import (
	"log"
)

func (l AndroidLogger) Write(p []byte) (int, error) {
	C.__android_log_write(l.level, C.CString("go_jni"+l.tag), C.CString(string(p)))
	return len(p), nil
}

type AndroidLogger struct {
	level C.int
	tag   string
}

type Lg struct {
	V *log.Logger
	D *log.Logger
	I *log.Logger
	W *log.Logger
	E *log.Logger
}

var lg = Lg{
	V: log.New(AndroidLogger{C.ANDROID_LOG_VERBOSE, "go_jni"}, "", 0),
	D: log.New(AndroidLogger{C.ANDROID_LOG_DEBUG, "go_jni"}, "", 0),
	I: log.New(AndroidLogger{C.ANDROID_LOG_INFO, "go_jni"}, "", 0),
	W: log.New(AndroidLogger{C.ANDROID_LOG_WARN, "go_jni"}, "", 0),
	E: log.New(AndroidLogger{C.ANDROID_LOG_ERROR, "go_jni"}, "", 0),
}

func init() {
	lg.D.Println("go_jni init")
}

//export go_add
func go_add(a, b int) int {
	lg.D.Println("go_add called")
	return a + b
}

//export go_hello
func go_hello(hello string)  *C.char {
	lg.D.Println("go_hello called with param: " + hello)
	return C.CString("go_hello: " + hello)
}

func main() {}
