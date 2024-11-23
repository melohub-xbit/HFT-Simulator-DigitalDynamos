#include <jni.h>
#include "OrderBook.h"  
#include "OrderBookNative.h"  // jni header
#include <string>
#include <unordered_map>

// Store native OrderBook pointers 
std::unordered_map<jlong, OrderBook*> orderBookInstances;
jlong globalId = 1; // For generating unique IDs

// Helper to retrieve the native OrderBook pointer
OrderBook* getOrderBookInstance(jlong handle) {
    return orderBookInstances[handle];
}

JNIEXPORT void JNICALL Java_exchange_OrderBook_createOrderBook(JNIEnv* env, jobject obj) {
    OrderBook* orderBook = new OrderBook();
    jlong handle = globalId++;
    orderBookInstances[handle] = orderBook;

    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    env->SetLongField(obj, nativeHandleField, handle);
}

JNIEXPORT void JNICALL Java_exchange_OrderBook_destroyOrderBook(JNIEnv* env, jobject obj) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    OrderBook* orderBook = getOrderBookInstance(handle);
    delete orderBook;
    orderBookInstances.erase(handle);
}

JNIEXPORT void JNICALL Java_exchange_OrderBook_addOrder(JNIEnv* env, jobject obj, jstring orderID, jstring type, jdouble price, jint quantity) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    const char* id = env->GetStringUTFChars(orderID, nullptr);
    const char* t = env->GetStringUTFChars(type, nullptr);

    OrderBook* orderBook = getOrderBookInstance(handle);
    orderBook->addOrder(std::string(id), std::string(t), price, quantity);

    env->ReleaseStringUTFChars(orderID, id);
    env->ReleaseStringUTFChars(type, t);
}

JNIEXPORT void JNICALL Java_exchange_OrderBook_cancelOrder(JNIEnv* env, jobject obj, jstring orderID) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    const char* id = env->GetStringUTFChars(orderID, nullptr);

    OrderBook* orderBook = getOrderBookInstance(handle);
    orderBook->cancelOrder(std::string(id));

    env->ReleaseStringUTFChars(orderID, id);
}

JNIEXPORT jdouble JNICALL Java_exchange_OrderBook_getBestBid(JNIEnv* env, jobject obj) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    OrderBook* orderBook = getOrderBookInstance(handle);
    return orderBook->getBestBid();
}

JNIEXPORT jdouble JNICALL Java_exchange_OrderBook_getBestAsk(JNIEnv* env, jobject obj) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    OrderBook* orderBook = getOrderBookInstance(handle);
    return orderBook->getBestAsk();
}


JNIEXPORT jobjectArray JNICALL Java_exchange_OrderBook_matchBuyOrder(JNIEnv* env, jobject obj, jstring orderID, jstring type, jdouble price, jint quantity) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    const char* id = env->GetStringUTFChars(orderID, nullptr);
    const char* t = env->GetStringUTFChars(type, nullptr);

    OrderBook* orderBook = getOrderBookInstance(handle);

    // Call the native method
    std::vector<std::vector<std::string>> results = orderBook->matchBuyOrder(std::string(id), std::string(t), price, quantity);

    // Prepare a 2D Java string array
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray outerArray = env->NewObjectArray(results.size(), env->FindClass("[Ljava/lang/String;"), nullptr);

    for (size_t i = 0; i < results.size(); ++i) {
        jobjectArray innerArray = env->NewObjectArray(results[i].size(), stringClass, nullptr);
        for (size_t j = 0; j < results[i].size(); ++j) {
            jstring str = env->NewStringUTF(results[i][j].c_str());
            env->SetObjectArrayElement(innerArray, j, str);
        }
        env->SetObjectArrayElement(outerArray, i, innerArray);
    }

    env->ReleaseStringUTFChars(orderID, id);
    env->ReleaseStringUTFChars(type, t);

    return outerArray;
}

JNIEXPORT jobjectArray JNICALL Java_exchange_OrderBook_matchSellOrder(JNIEnv* env, jobject obj, jstring orderID, jstring type, jdouble price, jint quantity) {
    jclass clazz = env->GetObjectClass(obj);
    jfieldID nativeHandleField = env->GetFieldID(clazz, "nativeHandle", "J");
    jlong handle = env->GetLongField(obj, nativeHandleField);

    const char* id = env->GetStringUTFChars(orderID, nullptr);
    const char* t = env->GetStringUTFChars(type, nullptr);

    OrderBook* orderBook = getOrderBookInstance(handle);

    // Call the native method
    std::vector<std::vector<std::string>> results = orderBook->matchSellOrder(std::string(id), std::string(t), price, quantity);

    // Prepare a 2D Java string array
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray outerArray = env->NewObjectArray(results.size(), env->FindClass("[Ljava/lang/String;"), nullptr);

    for (size_t i = 0; i < results.size(); ++i) {
        jobjectArray innerArray = env->NewObjectArray(results[i].size(), stringClass, nullptr);
        for (size_t j = 0; j < results[i].size(); ++j) {
            jstring str = env->NewStringUTF(results[i][j].c_str());
            env->SetObjectArrayElement(innerArray, j, str);
        }
        env->SetObjectArrayElement(outerArray, i, innerArray);
    }

    env->ReleaseStringUTFChars(orderID, id);
    env->ReleaseStringUTFChars(type, t);

    return outerArray;
}
