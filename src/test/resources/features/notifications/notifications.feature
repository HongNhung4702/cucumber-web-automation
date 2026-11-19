
@student @notifications
Feature: Kiểm thử thông báo cá nhân
  Là sinh viên đã đăng nhập
  Tôi muốn xem và tìm kiếm thông báo cá nhân
  Để kiểm tra dữ liệu hiển thị phù hợp với kết quả mong đợi

  Background:
    Given sinh viên đã đăng nhập bằng tài khoản kiểm thử hợp lệ

  @TC_10 @ui
  Scenario: Hiển thị danh sách thông báo cá nhân
    When sinh viên mở danh sách thông báo cá nhân
    Then bảng thông báo cá nhân hiển thị các cột đã xác nhận
    And danh sách thông báo cá nhân có dữ liệu

  @TC_10 @data-verification
  Scenario: Đối chiếu danh sách thông báo với dữ liệu tham chiếu
    When sinh viên mở danh sách thông báo cá nhân
    Then danh sách thông báo khớp dữ liệu tham chiếu TC_10

  @TC_11 @search @data-verification
  Scenario: Tìm kiếm thông báo với từ khóa có trong dữ liệu
    When sinh viên tìm kiếm thông báo cá nhân với từ khóa "08"
    Then kết quả tìm kiếm thông báo khớp dữ liệu tham chiếu TC_11

  @TC_12 @search @empty-result
  Scenario: Tìm kiếm thông báo với từ khóa không có trong dữ liệu
    When sinh viên tìm kiếm thông báo không tồn tại với từ khóa "Giấy phép"
    Then danh sách thông báo hiển thị trạng thái không có kết quả
