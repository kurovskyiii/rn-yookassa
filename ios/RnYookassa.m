#import "RnYookassa-Bridging-Header.h"

@interface RCT_EXTERN_MODULE(RnYookassa, RCTViewManager)

RCT_EXTERN_METHOD(tokenize:(NSDictionary *)info
                  callbacker:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(confirmPayment:(NSString *)url
                  callbacker:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(cancel)

@end

