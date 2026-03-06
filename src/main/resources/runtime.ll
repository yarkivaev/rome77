@.fmt_out = private unnamed_addr constant [5 x i8] c"%ld\0A\00"
@.fmt_in  = private unnamed_addr constant [4 x i8] c"%ld\00"

declare i32 @printf(ptr, ...)
declare i32 @scanf(ptr, ...)

define void @rome77_output(i64 %val) {
  call i32 (ptr, ...) @printf(ptr @.fmt_out, i64 %val)
  ret void
}

define i64 @rome77_input() {
  %buf = alloca i64
  store i64 0, ptr %buf
  call i32 (ptr, ...) @scanf(ptr @.fmt_in, ptr %buf)
  %result = load i64, ptr %buf
  ret i64 %result
}
