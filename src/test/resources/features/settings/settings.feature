
@settings @ui
Feature: Tùy chỉnh giao diện hệ thống
  Là người dùng Cổng thông tin đào tạo
  Tôi muốn thay đổi các thiết lập giao diện
  Để sử dụng website theo tùy chọn của mình

  @TC_43 @smoke
  Scenario: Mở giao diện tùy chỉnh hệ thống
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    When người dùng nhấn biểu tượng Cài đặt
    Then bảng Settings được hiển thị
    And bảng Settings có tùy chọn Theme
    And bảng Settings có tùy chọn Responsive font sizes
    And bảng Settings có tùy chọn Compact
    And bảng Settings có tùy chọn Rounded Corners
    And nút Save Settings được hiển thị

  @TC_44
  Scenario: Thay đổi giao diện sang chế độ Dark
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    And người dùng đã mở bảng Settings
    When người dùng chọn Theme là "Dark"
    And người dùng nhấn Save Settings
    Then bảng Settings tự đóng
    And giao diện website được áp dụng chế độ Dark
    When người dùng mở lại bảng Settings
    Then tùy chọn Theme hiển thị giá trị "Dark"
    When người dùng chọn Theme là "Light"
    And người dùng nhấn Save Settings
    Then giao diện website trở về chế độ Light

  @TC_45
  Scenario: Bật và tắt chế độ cỡ chữ thích ứng
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    And người dùng đã mở bảng Settings
    When người dùng bật Responsive font sizes
    And người dùng nhấn Save Settings
    Then bảng Settings tự đóng
    And thiết lập Responsive font sizes được lưu ở trạng thái bật
    When người dùng thay đổi kích thước cửa sổ trình duyệt sang kích thước nhỏ
    Then cỡ chữ được hiển thị phù hợp với kích thước cửa sổ khi bật chế độ thích ứng
    When người dùng mở lại bảng Settings
    And người dùng tắt Responsive font sizes
    And người dùng nhấn Save Settings
    Then thiết lập Responsive font sizes được lưu ở trạng thái tắt
    When người dùng thay đổi kích thước cửa sổ trình duyệt sang kích thước nhỏ
    Then chế độ cỡ chữ thích ứng không còn được áp dụng
    When người dùng khôi phục kích thước cửa sổ trình duyệt ban đầu

  @TC_46
  Scenario: Bật và tắt chế độ hiển thị thu gọn
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    And người dùng đã mở bảng Settings
    When người dùng bật Compact
    And người dùng nhấn Save Settings
    Then bảng Settings tự đóng
    And bố cục website được hiển thị theo chế độ thu gọn
    When người dùng mở lại bảng Settings
    Then tùy chọn Compact ở trạng thái bật
    When người dùng tắt Compact
    And người dùng nhấn Save Settings
    Then bố cục website trở về chế độ hiển thị thông thường

  @TC_47
  Scenario: Bật và tắt chế độ bo tròn góc
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    And người dùng đã mở bảng Settings
    When người dùng bật Rounded Corners
    And người dùng nhấn Save Settings
    Then bảng Settings tự đóng
    And các thành phần giao diện được áp dụng kiểu bo tròn góc
    When người dùng mở lại bảng Settings
    Then tùy chọn Rounded Corners ở trạng thái bật
    When người dùng tắt Rounded Corners
    And người dùng nhấn Save Settings
    Then các thành phần giao diện không còn áp dụng kiểu bo tròn góc

  @TC_48
  Scenario: Lưu và duy trì cấu hình tùy chỉnh giao diện
    Given người dùng đang truy cập trang chủ Cổng thông tin đào tạo
    And người dùng đã mở bảng Settings
    When người dùng thiết lập Theme là "Dark"
    And người dùng bật Responsive font sizes
    And người dùng tắt Compact
    And người dùng bật Rounded Corners
    And người dùng nhấn Save Settings
    Then bảng Settings tự đóng
    And giao diện website áp dụng các thiết lập đã chọn
    When người dùng mở lại bảng Settings
    Then Theme có giá trị "Dark"
    And Responsive font sizes ở trạng thái bật
    And Compact ở trạng thái tắt
    And Rounded Corners ở trạng thái bật
    When người dùng đóng bảng Settings
    And người dùng tải lại trang
    And người dùng mở lại bảng Settings
    Then Theme vẫn có giá trị "Dark"
    And Responsive font sizes vẫn ở trạng thái bật
    And Compact vẫn ở trạng thái tắt
    And Rounded Corners vẫn ở trạng thái bật
    And giao diện website vẫn áp dụng các thiết lập đã lưu
