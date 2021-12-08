#import "RnYookassa-Bridging-Header.h"

@interface RCT_EXTERN_MODULE(RnYookassa, RCTViewManager)

RCT_EXTERN_METHOD(tokenize:(NSDictionary *)params
                  callbacker:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(confirmPayment:(NSDictionary *)params
                  callbacker:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(dismiss)

@end

