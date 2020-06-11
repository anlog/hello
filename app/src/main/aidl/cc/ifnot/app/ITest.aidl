// ITest.aidl
package cc.ifnot.app;

// Declare any non-default types here with import statements

interface ITest {

    void iTest0(int a);

    // no reply back; so service will do one by one and no callback to client;
    // then client will have much time to request too much times;
    // so the binder buffer will be full (> 1M) when transact data.
    oneway void iTest1(int a);

    // test TransactTooLarge

    oneway void transact(String msg);

    int iTest2(int a);

}
