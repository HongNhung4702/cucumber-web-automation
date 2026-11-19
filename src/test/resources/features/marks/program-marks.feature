
@student @marks @program-marks
Feature: Kiểm thử điểm theo chương trình đào tạo
  Là sinh viên đã đăng nhập
  Tôi muốn xem điểm theo chương trình Kỹ thuật phần mềm
  Để kiểm tra các học phần và kết quả học tập

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_34 @ui
  Scenario: Hiển thị đúng bảng điểm chương trình đào tạo
    When sinh viên mở bảng điểm chương trình đào tạo TC_34
    Then chương trình đào tạo và cấu trúc bảng điểm TC_34 hiển thị đúng

  @TC_34 @baseline @data-verification
  Scenario: Đối chiếu dữ liệu bảng điểm theo chương trình đào tạo
    When sinh viên mở bảng điểm chương trình đào tạo TC_34
    Then toàn bộ điểm chương trình đào tạo TC_34 khớp baseline
