package common

import "fmt"

type S struct {
	Password string
}

func (s *S) SetPwd(password string) {
	s.Password = password
}

func Z(password string) {
	fmt.Println(password)
}

func SetPassord(password string) {

}

func A(i int, j int) {
	s := S{
		Password: "abc123",
	}
	fmt.Println(s)
	Z("z123")
	Z()
	fmt.Println()
	SetPassord("123", "456")

}

var Password string = "ggogogogo"
