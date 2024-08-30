import React from 'react';
import { NavLink } from 'react-router-dom';
import styled from 'styled-components';
import classNames from 'classnames';
import { buttonColor } from '../../styles/color';
import fontSize from '../../styles/fontSize';

const  dark = buttonColor;
const { medium } = fontSize;
const Wrapper = styled.nav`
  padding: 10px 0;
  display: flex;
  height: 55px;
  margin: 20px;
  a {
    background-color: yellowgreen;
    padding: 0 25px;
    border-radius: 10px;
    font-size: ${medium};
    line-height: 35px;
  }

  a:hover,
  a.on {
    background-color: ${dark[2]};
  }

  a + a {
    margin-left: 10px;
  }
`;

const TabMenus = ({ items }) => {
  return (
    items &&
    items.length > 0 && (
      <Wrapper>
        {items.map(({ name, link }) => (
          <NavLink
            to={link}
            key={link}
            className={({ isActive }) => classNames({ on: isActive })}
          />
        ))}
      </Wrapper>
    )
  );
};
export default React.memo(TabMenus);
