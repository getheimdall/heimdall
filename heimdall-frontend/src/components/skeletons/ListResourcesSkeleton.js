import React from 'react'
import {Row, Skeleton } from 'antd'
import HeimdallCollapse from "../collapse"

const HeimdallPanel = HeimdallCollapse.Panel

const ListResourcesSkeleton = () => (
    <Row>
        <HeimdallCollapse onChange={this.callback}>
            <HeimdallPanel className={"header-tour"} header={<Skeleton title={{width: '50%'}} paragraph={false} />} key={0} extra={
                <Row type="flex" justify="center">
                    <Skeleton active title paragraph={false} />
                </Row>
            } extraWidth={10}>
                <Skeleton />
            </HeimdallPanel>
        </HeimdallCollapse>
        <HeimdallCollapse onChange={this.callback}>
            <HeimdallPanel className={"header-tour"} header={<Skeleton title={{width: '50%'}} paragraph={false} />} key={1} extra={
                 <Row type="flex" justify="center">
                    <Skeleton active title paragraph={false}/>
                </Row>
            } extraWidth={10}>
                <Skeleton />
            </HeimdallPanel>
        </HeimdallCollapse>
        <HeimdallCollapse onChange={this.callback}>
            <HeimdallPanel className={"header-tour"} header={<Skeleton title={{width: '50%'}} paragraph={false} />} key={2} extra={
                 <Row type="flex" justify="center">
                    <Skeleton active title paragraph={false}/>
                </Row>
            } extraWidth={10}>
                <Skeleton />
            </HeimdallPanel>
        </HeimdallCollapse>
        <HeimdallCollapse onChange={this.callback}>
            <HeimdallPanel className={"header-tour"} header={<Skeleton title={{width: '50%'}} paragraph={false} />} key={3} extra={
                 <Row type="flex" justify="center">
                    <Skeleton active title paragraph={false}/>
                </Row>
            } extraWidth={10}>
                <Skeleton />
            </HeimdallPanel>
        </HeimdallCollapse>
    </Row>
)

export default ListResourcesSkeleton