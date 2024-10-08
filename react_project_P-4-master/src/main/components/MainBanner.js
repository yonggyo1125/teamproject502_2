import React from 'react';
import styled from 'styled-components';
import { color } from '../../styles/color';
import SlideBanner from '../../commons/components/SlideBanner';
import banner1 from '../../images/banner/banner1.png';
import banner2 from '../../images/banner/banner2.png';

const Wrapper = styled.div`
  div {
    margin: 0 auto 50px;
  }

  img {
    width: 100%;
    height: 100%;
  }
`;

const options = {
  loop: true,
  speed: 1000,
  pagination: true,
  navigation: true,
  autoplay: 4000,
};

const items = [
  { image: banner1, link: '/ai', alt: '배너1' },
  { image: banner2, link: '/ai', alt: '배너2' },
];

const StyleSlideBanner = styled(SlideBanner)`
  .swiper-pagination {
    bottom: 10px;
  }

  .swiper-pagination-bullet {
    width: 12px;
    height: 12px;
    background: #000;
    opacity: 0.3;
  }
  .swiper-pagination-bullet-active {
    background: ${color.jmt};
    opacity: 0.8;
  }

  .swiper-button-prev,
  .swiper-button-next {
    top: calc(50% - 60px);
    color: #ececec;
    opacity: 0.8;
  }
  .swiper-button-prev {
    left: calc(50% - 900px);
  }

  .swiper-button-next {
    right: calc(50% - 900px);
  }
`;

const MainBanner = ({}) => {
  return (
    <Wrapper>
      <StyleSlideBanner
        items={items}
        width={1920}
        height={540}
        options={options}
      />
    </Wrapper>
  );
};

export default React.memo(MainBanner);
