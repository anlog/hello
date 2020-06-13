//
// Created by hello on 2020/6/13.
//

#include <gtest/gtest.h>
#include "../foo.h"

TEST(HelloWorldTest, PrintHelloWorld) {
    printf("Hello, World!");
}

TEST(Foo, Bar) {
    add(1, 2);
}

TEST(FooTest, ZeroZero) {
    EXPECT_EQ(0, add(0, 0));
}

TEST(FooTest, OneOne) {
    EXPECT_EQ(2, add(1, 1));
}

