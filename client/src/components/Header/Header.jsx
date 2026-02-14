import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './Header.module.scss';
import currentLocationIcon from './imgs/current-location.png';
import toast from 'react-hot-toast';

const Header = ({ onSearch, onCurrentLocation, defaultValue = '', isLoading = false }) => {
    const [value, setValue] = useState(defaultValue);
    
    useEffect(() => {
        setValue(defaultValue);
    }, [defaultValue]);

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(value);
    };

    const handleCurrentLocationClick = () => {
        const OK = 500; // 0.5km
        const WARN = 2000; // 2km

        navigator.geolocation.getCurrentPosition(
            // 첫 번째 인자 : 성공 시 실행될 콜백 함수
            (pos) => {
                const { longitude, latitude, accuracy } = pos.coords;
                /*
                    if(accuracy > WARN) {
                        toast.error(
                            '정확한 위치를 가져올 수 없는 환경입니다. \n' +
                            '검색을 통해 날씨를 조회해 주세요.'
                        );

                        return;
                    }
                */

                if(accuracy > OK) toast('현재 위치 정확도가 낮아, 약간의 오차가 있을 수 있습니다.');

                // longitude : x(경도)
                // latitude : y(위도)
                onCurrentLocation(longitude, latitude);
            },
            // 두 번째 인자 : 실패 시 실행될 콜백 함수
            (err) => {
                console.error(err.code, err.message);
                toast.error("위치 권한을 허용해 주세요.");
            },
            /*
                // 세 번째 인자 : 옵션 객체
                {
                    enableHighAccuracy: true,   // 고정밀 위치 측정 사용(정확도는 향상되지만, 배터리 소모/응답 시간 증가)
                    timeout: 7000,              // 7초 안에 위치를 못 가져오면, 실패 처리
                    maximumAge: 180000          // 3분 이내에 측정된 기존 위치가 있으면, 새로 측정하지 않고 재사용(0 : 항상 새로 측정)
                }
            */
        );
    };

    return (
        <main className={styles.wrapper}>
            <section className={styles.searchSection}>
                <form className={styles.searchForm} onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="지역을 입력해 주세요."
                        className={styles.searchInput}
                        value={value}
                        onChange={(e) => setValue(e.target.value)}
                        disabled={isLoading}
                    />
                </form>
                <img className={styles.currentLocationIcon} src={currentLocationIcon} title="내 위치 날씨 조회" onClick={handleCurrentLocationClick} />
            </section>
            <nav className={styles.nav}>
                <Link to="/login" className={styles.link}>로그인</Link>
                <Link to="/join" className={styles.link}>회원가입</Link>
            </nav>
        </main>
    );
};

export default Header;