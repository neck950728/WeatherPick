import React, { useEffect, useMemo } from 'react';
import { apiFetch } from '../../api/api';
import styles from './Home.module.scss';

const weatherIcons = import.meta.glob('./imgs/*.png', {eager: true, import: 'default' });
const getWeatherIconSrc = (skyType, precipType) => {
    const key = `./imgs/${skyType}_${precipType}.png`;
    return weatherIcons[key];
};

const Home = () => {
    const mockResponse = {
        weather: {
            regionLabel: '갈산역',
            tempC: 3,
            precipitation1hMm: 0,
            humidity: 42,
            windSpeedMs: 1.8,
            precipType: 'RAIN',
            skyType: 'CLEAR'
        },
        message: '- 옷차림 : 니트, 자켓, 청바지\n- 준비물 : 마스크, 손수건'
    };

    useEffect(() => {
        /*
            apiFetch(`/api/weather/now?region=갈산역`)
                .then(res => {
                    console.log(res);
                });
        */
    }, []);

    const viewModel = useMemo(() => {
        const { weather } = mockResponse;

        const precipLabelMap = {
            NONE: '',
            RAIN: '비',
            RAIN_SNOW: '진눈깨비',
            SNOW: '눈',
            DRIZZLE: '이슬비',
            DRIZZLE_SNOW: '이슬비 + 눈 날림',
            SNOW_FLURRY: '눈 날림'
        };

        const skyLabelMap = {
            CLEAR: '맑음',
            PARTLY_CLOUDY: '구름 많음',
            CLOUDY: '흐림'
        };

        const precipLabel = precipLabelMap[weather.precipType];
        const skyLabel = skyLabelMap[weather.skyType];

        const now = new Date();
        const weekdayMap = {
            0: '일요일',
            1: '월요일',
            2: '화요일',
            3: '수요일',
            4: '목요일',
            5: '금요일',
            6: '토요일'
        };

        const hours = now.getHours();
        const isPm = hours >= 12;
        const displayHour = ((hours + 11) % 12) + 1; // 24시간제(0 ~ 23시) → 12시간제(1 ~ 12시) 변환
        const minutes = String(now.getMinutes()).padStart(2, '0'); // ex) 1 → 01
        const dayText = `(${weekdayMap[now.getDay()]})`;
        const timeText = `${isPm ? 'PM' : 'AM'} ${displayHour}:${minutes}`;
        const conditionText = precipLabel !== '' ? `${skyLabel} · ${precipLabel}` : skyLabel;
        const iconSrc = getWeatherIconSrc(weather.skyType, weather.precipType);

        return {
            regionLabel: weather.regionLabel,
            tempC: weather.tempC,
            precipitation1hMm: weather.precipitation1hMm,
            humidity: weather.humidity,
            windSpeedMs: weather.windSpeedMs,
            dayText,
            timeText,
            conditionText,
            iconSrc
        };
    }, []);

    return (
        <main>
            <section>
                <article>
                    <div className={styles.weatherCard}>
                        <header className={styles.searchResult}>
                            검색 결과 : <span className={styles.region}>{viewModel.regionLabel}</span>
                        </header>

                        <section className={styles.cardBody}>
                            <section className={styles.left}>
                                <div className={styles.iconTempRow}>
                                    <img className={styles.weatherIcon} src={viewModel.iconSrc} />
                                    <div className={styles.tempBlock}>
                                        <div className={styles.tempRow}>
                                            <span className={styles.temp}>{viewModel.tempC}</span>
                                            <span className={styles.tempUnit}>°C</span>
                                        </div>
                                        <div className={styles.metrics}>
                                            <div className={styles.metric}>1시간 강수량 : {viewModel.precipitation1hMm}mm</div>
                                            <div className={styles.metric}>습도 : {viewModel.humidity}%</div>
                                            <div className={styles.metric}>풍속 : {viewModel.windSpeedMs}m/s</div>
                                        </div>
                                    </div>
                                </div>
                            </section>

                            <aside className={styles.right}>
                                <div className={styles.title}>날씨</div>
                                <div className={styles.datetime}>
                                    <span className={styles.day}>{viewModel.dayText}</span>
                                    <time className={styles.time}>{viewModel.timeText}</time>
                                </div>
                                <div className={styles.condition}>{viewModel.conditionText}</div>
                            </aside>
                        </section>
                    </div>
                </article>

                <article>
                    <pre className={styles.recommendation}>{mockResponse.message}</pre>
                </article>
            </section>
        </main>
    );
};

export default Home;